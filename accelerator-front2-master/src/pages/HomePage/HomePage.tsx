import React from 'react'
import { Box } from '@mui/material'
import HeroSection from './HeroSection'
import FeaturesSection from './FeaturesSection'
import BenefitsSection from './BenefitsSection'
import CtaSection from './CtaSection'

const HomePage: React.FC = () => {
	return (
		<>
			<Box mt={12}>
				<HeroSection />
			</Box>
			<Box mt={12}>
				<BenefitsSection />
			</Box>
			<Box mt={12}>
				<FeaturesSection />
			</Box>
			<Box sx={{ mt: { xs: 6, md: 8 } }}>
				<CtaSection />
			</Box>
		</>
	)
}

export default HomePage
